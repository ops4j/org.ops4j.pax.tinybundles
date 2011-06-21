package org.ops4j.pax.tinybundles.core;

/**
 * The strategy used when a class added has inner classes.
 * NONE means no other class will be added. ALL means all
 * inner classes are added recursively. ANONYMOUS mean
 * only anynomous classes will be added. 
 * 
 * @author <a href="mailto:rafaelliu@gmail.com">Rafael Liu</a>
 *
 */
public enum InnerClassStrategy {
	
	NONE, ALL, ANONYMOUS

}
