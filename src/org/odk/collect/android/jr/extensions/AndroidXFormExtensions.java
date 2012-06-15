/**
 * 
 */
package org.odk.collect.android.jr.extensions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import org.javarosa.core.model.XFormExtension;
import org.javarosa.core.util.externalizable.DeserializationException;
import org.javarosa.core.util.externalizable.ExtUtil;
import org.javarosa.core.util.externalizable.ExtWrapMap;
import org.javarosa.core.util.externalizable.PrototypeFactory;

/**
 * @author ctsims
 *
 */
public class AndroidXFormExtensions implements XFormExtension {
	Hashtable<String, IntentCallout> intents = new Hashtable<String, IntentCallout>(); 
	
	public AndroidXFormExtensions() {
		
	}
	
	public void registerIntent(String id, IntentCallout callout) {
		intents.put(id, callout);
	}
	
	public IntentCallout getIntent(String id) {
		return intents.get(id);
	}

	@Override
	public void readExternal(DataInputStream in, PrototypeFactory pf) throws IOException, DeserializationException {
		intents = (Hashtable<String, IntentCallout>)ExtUtil.read(in, new ExtWrapMap(String.class, IntentCallout.class), pf);
	}

	@Override
	public void writeExternal(DataOutputStream out) throws IOException {
		ExtUtil.write(out,  new ExtWrapMap(intents));
	}

	
}