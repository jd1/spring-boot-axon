package info.novatec.axon.account;

import info.novatec.axon.account.BankAccount.InsufficientBalanceException;
import info.novatec.axon.account.command.CloseAccountCommand;
import info.novatec.axon.account.command.CreateAccountCommand;
import info.novatec.axon.account.command.DepositMoneyCommand;
import info.novatec.axon.account.command.WithdrawMoneyCommand;
import info.novatec.axon.account.event.AccountClosedEvent;
import info.novatec.axon.account.event.AccountCreatedEvent;
import info.novatec.axon.account.event.MoneyDepositedEvent;
import info.novatec.axon.account.event.MoneyWithdrawnEvent;
import org.axonframework.commandhandling.model.AggregateNotFoundException;
import org.axonframework.eventsourcing.eventstore.EventStoreException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class BankAccountTest {
    private FixtureConfiguration fixture;

    @BeforeEach
    public void setUp() {
        fixture = new AggregateTestFixture(BankAccount.class);
    }

    @Test
    public void createAccount() {
        fixture.given()
                .when(new CreateAccountCommand("id", "Max"))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new AccountCreatedEvent("id", "Max", 0.0));
    }

    @Test
    public void createExistingAccount() {
        fixture.given(new AccountCreatedEvent("id", "Max", 0.0))
                .when(new CreateAccountCommand("id", "Max"))
                .expectException(EventStoreException.class);
    }

    @Test
    public void depositMoney() {
        fixture.given(new AccountCreatedEvent("id", "Max", 0.0))
                .when(new DepositMoneyCommand("id", 12.0))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new MoneyDepositedEvent("id", 12.0));
    }

    @Test
    public void depositMoneyOnInexistentAccount() {
        fixture.given()
                .when(new DepositMoneyCommand("id", 12.0))
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    public void withdrawMoney() {
        fixture.given(new AccountCreatedEvent("id", "Max", 0.0),
                new MoneyDepositedEvent("id", 10.0))
                .when(new WithdrawMoneyCommand("id", 5.0))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new MoneyWithdrawnEvent("id", 5.0));
    }

    @Test
    public void overdrawAccount() {
        fixture.given(new AccountCreatedEvent("id", "Max", 0.0),
                new MoneyDepositedEvent("id", 10.0))
                .when(new WithdrawMoneyCommand("id", 20.0))
                .expectException(InsufficientBalanceException.class);
    }

    @Test
    public void closeAccount() {
        fixture.given(new AccountCreatedEvent("id", "Max", 0.0),
                new MoneyDepositedEvent("id", 10.0))
                .when(new CloseAccountCommand("id"))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new AccountClosedEvent("id"));
    }

    @Test
    public void createAndDeposit() {
        BankAccount account = new BankAccount();
        account.on(new AccountCreatedEvent("id", "Max", 0.0));
        account.on(new MoneyDepositedEvent("id", 10.0));

        assertEquals(10.0, account.getBalance());
    }

    @Test
    public void noAggregateLifecycle() {
        BankAccount account = new BankAccount();
        account.on(new AccountCreatedEvent("id", "Max", 0.0));

        assertThrows(IllegalStateException.class, () -> account.on(new DepositMoneyCommand("id", 10.0)));
    }
}
