package io.pivotal.pde.dataserializable.sample;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.geode.DataSerializable;
import org.apache.geode.DataSerializer;

import io.pivotal.pde.sample.Address;

public class DataSerializableAddress extends Address implements DataSerializable {

	private static final long serialVersionUID = 6409874823184313771L;

	@Override
	public void toData(DataOutput out) throws IOException {
		DataSerializer.writeString(getStreet(), out);
		DataSerializer.writeString(getCity(), out);
		DataSerializer.writeString(getState(), out);
		DataSerializer.writeString(getZip(), out);
	}

	@Override
	public void fromData(DataInput in) throws IOException, ClassNotFoundException {
		setStreet(DataSerializer.readString(in));
		setCity(DataSerializer.readString(in));
		setState(DataSerializer.readString(in));
		setZip(DataSerializer.readString(in));
	}
	

}
