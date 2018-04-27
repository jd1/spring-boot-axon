package info.novatec.axon.account.command;

import info.novatec.axon.BaseCommand;

public class CreateAccountCommand extends BaseCommand<String>{
	public final String accountCreator;

	public CreateAccountCommand(String id, String accountCreator) {
		super(id);
		this.accountCreator = accountCreator;
	}
}
