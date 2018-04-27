package info.novatec.axon.account;

import info.novatec.axon.account.command.CloseAccountCommand;
import info.novatec.axon.account.command.CreateAccountCommand;
import info.novatec.axon.account.command.DepositMoneyCommand;
import info.novatec.axon.account.command.WithdrawMoneyCommand;
import info.novatec.axon.account.event.AccountClosedEvent;
import info.novatec.axon.account.event.AccountCreatedEvent;
import info.novatec.axon.account.event.MoneyDepositedEvent;
import info.novatec.axon.account.event.MoneyWithdrawnEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;

import java.io.Serializable;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class BankAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	@AggregateIdentifier
	private String id;

	private double balance;

	private String owner;

	@CommandHandler
	public BankAccount(CreateAccountCommand command) {
		String id = command.id;
		String name = command.accountCreator;

		Assert.hasLength(id, "Missin id");
		Assert.hasLength(name, "Missig account creator");

		AggregateLifecycle.apply(new AccountCreatedEvent(id, name, 0));
	}

	public BankAccount() {
		// constructor needed for reconstruction
	}
	
	@EventSourcingHandler
	protected void on(AccountCreatedEvent event) {
		this.id = event.id;
		this.owner = event.accountCreator;
		this.balance = event.balance;
	}
	
	@CommandHandler
	protected void on(CloseAccountCommand command) {
		apply(new AccountClosedEvent(id));
	}
	
	@EventSourcingHandler
	protected void on(AccountClosedEvent event) {
		AggregateLifecycle.markDeleted();
	}

	@CommandHandler
	protected void on(DepositMoneyCommand command) {
		double ammount = command.ammount;

		Assert.isTrue(ammount > 0.0, "Deposit must be a positiv number.");

		AggregateLifecycle.apply(new MoneyDepositedEvent(id, ammount));
	}



	@EventSourcingHandler
	protected void on(MoneyDepositedEvent event) {
		this.balance += event.amount;
	}

	@CommandHandler
	protected void on(WithdrawMoneyCommand command) {
		double amount = command.amount;

		Assert.isTrue(amount > 0.0, "Withdraw must be a positiv number.");

		if(balance - amount < 0) {
		    throw new InsufficientBalanceException("Insufficient balance. Trying to withdraw: " + amount + ", but current balance is: " + balance);
        }

		AggregateLifecycle.apply(new MoneyWithdrawnEvent(id, amount));
	}

    public static class InsufficientBalanceException extends RuntimeException {
        InsufficientBalanceException(String message) {
            super(message);
        }
    }

	@EventSourcingHandler
	protected void on(MoneyWithdrawnEvent event) {
		this.balance -= event.amount;
	}

}
