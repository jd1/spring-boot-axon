package info.novatec.axon;

import org.springframework.util.Assert;

public class BaseEvent<T> {

	public final T id;
	
	public BaseEvent(T id) {
		Assert.notNull(id, "Id cannot be null");
		this.id = id;
	}
}
