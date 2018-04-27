package info.novatec.axon.account.command;

import info.novatec.axon.BaseCommand;

public class DepositMoneyCommand extends BaseCommand<String> {
	public final double ammount;

	public DepositMoneyCommand(String id, double ammount) {
		super(id);
		this.ammount = ammount;
	}
}
