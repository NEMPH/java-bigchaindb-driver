package com.authenteq.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import com.authenteq.api.TransactionsApi;
import com.authenteq.builders.BigchainDbConfigBuilder;
import com.authenteq.builders.BigchainDbTransactionBuilder;
import com.authenteq.constants.Operations;
import com.authenteq.model.Transaction;
import com.authenteq.util.JsonUtils;
import com.authenteq.model.Asset;
import com.authenteq.model.DataModel;
import com.authenteq.model.GenericCallback;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import okhttp3.Response;

/**
 * The Class BigchaindbTransactionTest.
 */
public class TransactionApiTest {

	/**
	 * Inits the.
	 */
	@Before
	public void init() {
		BigchainDbConfigBuilder.baseUrl("https://test.ipdb.io").addToken("app_id", "2bbaf3ff")
				.addToken("app_key", "c929b708177dcc8b9d58180082029b8d").setup();
	}


	/**
	 * Test post transaction using builder.
	 */
	@Test
	public void testPostTransactionUsingBuilder() {

		net.i2p.crypto.eddsa.KeyPairGenerator edDsaKpg = new net.i2p.crypto.eddsa.KeyPairGenerator();
		KeyPair keyPair = edDsaKpg.generateKeyPair();
		try {
			Transaction transaction = BigchainDbTransactionBuilder.init().addAsset("middlename", "mname")
					.addAsset("firstname", "John")
					.addAsset("giddlename", "mname")
					.addAsset("ziddlename", "mname")
					.addAsset("lastname", "Smith")
					.addMetaData("what", "My first BigchainDB transaction")
					.buildAndSign((EdDSAPublicKey) keyPair.getPublic(), (EdDSAPrivateKey) keyPair.getPrivate())
					.sendTransaction();

			assertNotNull(transaction.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPostTransactionOfObjectUsingBuilder() {
		net.i2p.crypto.eddsa.KeyPairGenerator edDsaKpg = new net.i2p.crypto.eddsa.KeyPairGenerator();
		KeyPair keyPair = edDsaKpg.generateKeyPair();
		try {
			ObjectDummy dummyAsset = new ObjectDummy();
			dummyAsset.setId("id");
			dummyAsset.setDescription("asset");
			System.out.println(dummyAsset.toMapString());
			
			ObjectDummy dummyMeta = new ObjectDummy();
			dummyMeta.setId("id");
			dummyMeta.setDescription("meta");
			
			Transaction transaction = BigchainDbTransactionBuilder.init()
					.addAssets(dummyAsset)
					.addMetaData(dummyMeta)
					.buildAndSign((EdDSAPublicKey) keyPair.getPublic(), (EdDSAPrivateKey) keyPair.getPrivate())
					.sendTransaction();
			assertNotNull(transaction.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test post transaction using builder with call back.
	 */
	@Test
	public void testPostTransactionUsingBuilderWithCallBack() {
		net.i2p.crypto.eddsa.KeyPairGenerator edDsaKpg = new net.i2p.crypto.eddsa.KeyPairGenerator();
		KeyPair keyPair = edDsaKpg.generateKeyPair();
		try {
			BigchainDbTransactionBuilder.init().addAsset("firstname", "alvin").addMetaData("what", "bigchaintrans")
					.buildAndSign((EdDSAPublicKey) keyPair.getPublic(), (EdDSAPrivateKey) keyPair.getPrivate())
					.sendTransaction(new GenericCallback() {

						@Override
						public void transactionMalformed(Response response) {
							// System.out.println(response.message());
							System.out.println("malformed " + response.message());
						}

						@Override
						public void pushedSuccessfully(Response response) {
							System.out.println("pushedSuccessfully");
						}

						@Override
						public void otherError(Response response) {
							System.out.println("otherError");

						}
					});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test transaction by asset id create.
	 */
	@Test
	public void testTransactionByAssetIdCreate() {
		try {

			System.out.println(TransactionsApi
					.getTransactionsByAssetId("437ce30de5cf1c3ad199fa983aded47d0db43567befa92e3a36b38a5784e4d3a",
							Operations.CREATE)
					.getTransactions().size());

			assertTrue(TransactionsApi
					.getTransactionsByAssetId("437ce30de5cf1c3ad199fa983aded47d0db43567befa92e3a36b38a5784e4d3a",
							Operations.CREATE)
					.getTransactions().size() > 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test transaction by asset id transfer.
	 */
	@Test
	public void testTransactionByAssetIdTransfer() {
		try {
			assertTrue(TransactionsApi
					.getTransactionsByAssetId("437ce30de5cf1c3ad199fa983aded47d0db43567befa92e3a36b38a5784e4d3a",
							Operations.CREATE)
					.getTransactions().size() > 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class ObjectDummy extends DataModel {
		private String id;
		private String description;
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}
}
