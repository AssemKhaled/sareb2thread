package sarebApp.com.sareb.entities;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Attributes {

	String attributes;

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	
	
}
