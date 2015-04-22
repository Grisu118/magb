package fhnw.util.math;

import fhnw.util.IFloatArrayCopyProvider;

public interface IVec4 extends IFloatArrayCopyProvider {
	float x();
	float y();
	float z();
	float w();
	
	Vec4 toVec4();
}
