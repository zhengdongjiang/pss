package com.bfs.pss.encode;

import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ZengFC
 *
 */
public class ObjectEncoder implements Encoder<Object>{
	public static final ObjectMapper mapper =  new ObjectMapper();
	
	static {
	}

	@Override
	public byte[] toBytes(Object arg0) {
		try {
			return mapper.writeValueAsBytes(arg0);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public ObjectEncoder() {
		super();
	}
	
	public ObjectEncoder(VerifiableProperties prop) {
		super();
	}

}
