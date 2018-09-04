package com.wondertek.mam.util.video;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondertek.mam.commons.MamConstants;

public class MosiacTaskQueueController extends AbstractTaskQueueController<Wrapper<MosiacChain>> {

	public MosiacTaskQueueController() {
		this.type = "mosiac";
	}

	@Override
	public void doit() {
		final Log log = LogFactory.getLog(getClass());
		Wrapper<MosiacChain> wrapper = getchain();
		if (wrapper.chain != null) {
			String ret = wrapper.chain.doChain();
			log.debug("dochain ret:" + ret + ", chain:" + wrapper.chain);
			String output = wrapper.chain.getParam0().get(0).get("output");
			ServletUtil(wrapper.ip + "/acceptMessageServer?optype=mosiacVideo&id="
					+ wrapper.id + "&output=" + output);
		}
	}

}
