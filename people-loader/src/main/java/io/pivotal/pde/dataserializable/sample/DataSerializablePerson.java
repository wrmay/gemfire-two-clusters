package io.pivotal.pde.dataserializable.sample;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.geode.DataSerializable;
import org.apache.geode.DataSerializer;

import io.pivotal.pde.sample.Address;
import io.pivotal.pde.sample.Person;

public class DataSerializablePerson extends Person implements DataSerializable{

	private static final long serialVersionUID = 809004664552056892L;

	@Override
	public void toData(DataOutput out) throws IOException {
		DataSerializer.writeObject(getAddress(),out);
		DataSerializer.writePrimitiveInt(getAge(), out);
		DataSerializer.writeString(getFirstName(), out);
		DataSerializer.writeString(getGender(), out);
		DataSerializer.writePrimitiveInt(getId(), out);
		DataSerializer.writeString(getLastName(), out);
		DataSerializer.writeArrayList(getPastAddresses(), out);
		DataSerializer.writeString(getPhone(), out);
	}

	@Override
	public void fromData(DataInput in) throws IOException, ClassNotFoundException {
		setAddress(DataSerializer.readObject(in));
		setAge(DataSerializer.readPrimitiveInt(in));
		setFirstName(DataSerializer.readString(in));
		setGender(DataSerializer.readString(in));
		setId(DataSerializer.readPrimitiveInt(in));
		setLastName(DataSerializer.readString(in));
		setPastAddresses(DataSerializer.readArrayList(in));
		setPhone(DataSerializer.readString(in));		
	}

	@Override
	protected Address newFakeAddress() {
		DataSerializableAddress result = new DataSerializableAddress();
		result.fake();
		return result;
	}

	
}
