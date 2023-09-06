package org.ops4j.pax.tinybundles.it;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.ops4j.pax.tinybundles.TinyBundles;
import org.ops4j.pax.tinybundles.TinyBundlesFactory;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TinyBundlesFactoryIT extends TinybundlesTestSupport {

    @Inject
    public TinyBundlesFactory factory;

    @Configuration
    public Option[] configuration() {
        return options(
            baseConfiguration()
        );
    }

    @Test
    public void testTinyBundlesFactory() {
        final TinyBundle bundle = factory.bundle();
        assertThat(bundle, notNullValue());
    }

    @Test
    public void testTinyBundles() {
        final TinyBundle bundle = TinyBundles.bundle();
        assertThat(bundle, notNullValue());
    }

}
