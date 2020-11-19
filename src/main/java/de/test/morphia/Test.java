package de.test.morphia;

import java.util.UUID;

import org.bson.UuidRepresentation;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;

import dev.morphia.Datastore;
import dev.morphia.DeleteOptions;
import dev.morphia.Morphia;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.mapping.codec.pojo.EntityModel;
import dev.morphia.query.experimental.filters.Filters;

public class Test {
	public static void main(String[] args) {

		final Datastore datastore = Morphia.createDatastore( //
				MongoClients.create( //
						MongoClientSettings.builder()
								.applyConnectionString(new ConnectionString("mongodb://10.9.1.235/"))
								.uuidRepresentation(UuidRepresentation.STANDARD).build() //
				), "morphia_example", MapperOptions.DEFAULT);

		datastore.getMapper().mapPackage("de.test.morphia");
//		datastore.getMapper().addInterceptor(new EntityInterceptor() {
//			@Override
//			public void preLoad(Object ent, Document document, Mapper mapper) {
//				System.out.println("test");
//			}
//		});

		datastore.ensureIndexes();

		EntityModel model = datastore.getMapper().map(GenericEntity.class).get(0);
		Class<?> idType = model.getField("id").getType();
		Class<?> testType = model.getField("test").getType();
		Class<?> test2Type = model.getField("test2").getType();

		model = datastore.getMapper().map(SpecificEntity.class).get(0);
		idType = model.getField("id").getType();
		testType = model.getField("test").getType();
		test2Type = model.getField("test2").getType();

		model = datastore.getMapper().map(MoreSpecificEntity.class).get(0);
		idType = model.getField("id").getType();
		testType = model.getField("test").getType();
		test2Type = model.getField("test2").getType();

		MoreSpecificEntity beforeDB = new MoreSpecificEntity();
		beforeDB.setId(UUID.randomUUID());
		beforeDB.setTest(UUID.randomUUID());
		beforeDB.setTest2(UUID.randomUUID());
		beforeDB.setNumber(13);
		beforeDB.setNumber2(14);
		datastore.save(beforeDB);

		MoreSpecificEntity fromDB = datastore.find(MoreSpecificEntity.class).filter(Filters.eq("_id", beforeDB.getId()))
				.first();

		System.out.println(fromDB);

		datastore.find(MoreSpecificEntity.class).delete(new DeleteOptions().multi(true));
	}

	@Entity
	private static class GenericEntity<T> {
		@Id
		protected T id;
		protected T test;
		protected UUID test2;

		public T getId() {
			return id;
		}

		public void setId(T id) {
			this.id = id;
		}

		public T getTest() {
			return test;
		}

		public void setTest(T test) {
			this.test = test;
		}

		public UUID getTest2() {
			return test2;
		}

		public void setTest2(UUID test2) {
			this.test2 = test2;
		}
	}

	@Entity
	private static class SpecificEntity<ID> extends GenericEntity<ID> {
		private long number;

		public long getNumber() {
			return number;
		}

		public void setNumber(long number) {
			this.number = number;
		}

	}

	@Entity
	private static class MoreSpecificEntity extends SpecificEntity<UUID> {
		private long number2;

		public long getNumber2() {
			return number2;
		}

		public void setNumber2(long number2) {
			this.number2 = number2;
		}
	}
}
