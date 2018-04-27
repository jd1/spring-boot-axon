package info.novatec.axon.account.command;

import info.novatec.axon.BaseCommand;

public class WithdrawMoneyCommand extends BaseCommand<String> {
	public final double amount;

	public WithdrawMoneyCommand(String id, double amount) {
		super(id);
		this.amount = amount;
	}
}
