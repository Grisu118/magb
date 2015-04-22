package fhnw.util.math;

import fhnw.util.IFloatArrayCopyProvider;


public interface IVec3 extends IFloatArrayCopyProvider {
	float x();
	float y();
	float z();
	
	Vec3 toVec3();
}
