package info.novatec.axon.account.event;

import info.novatec.axon.BaseEvent;

public class AccountCreatedEvent extends BaseEvent<String> {
	public final String accountCreator;
	public final double balance;

	public AccountCreatedEvent(String id, String accountCreator, double balance) {
		super(id);
		this.accountCreator = accountCreator;
		this.balance = balance;
	}
}
