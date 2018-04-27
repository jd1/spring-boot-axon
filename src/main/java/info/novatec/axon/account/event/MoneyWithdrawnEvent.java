package info.novatec.axon.account.event;

import info.novatec.axon.BaseEvent;

public class MoneyWithdrawnEvent extends BaseEvent<String> {
	public final double amount;

	public MoneyWithdrawnEvent(String id, double amount) {
		super(id);
		this.amount = amount;
	}
}
