package remixlab.bias.fx;

import remixlab.bias.core.*;

public interface MultiTempi {
	boolean initAction(BogusEvent event);
	boolean execAction(BogusEvent event);
	void flushAction(BogusEvent event);
}
