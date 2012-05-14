/*
 * Copyright (C) FuseSource, Inc.
 *   http://fusesource.com
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.fusesource.esb.itests.pax.exam.karaf;

import java.io.File;

import org.fusesource.tooling.testing.pax.exam.karaf.FuseTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openengsb.labs.paxexam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;


import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.logLevel;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.useOwnExamBundlesStartLevel;
import static org.ops4j.pax.exam.CoreOptions.maven;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class RiderAutoFeatureTest extends FuseTestSupport {

    static final String GROUP_ID = "org.fusesource.esb";
    static final String ARTIFACT_ID = "fuse-esb-medium";

    static final String KARAF_GROUP_ID = "org.apache.karaf";
    static final String KARAF_ARTIFACT_ID = "apache-karaf";
	
    @Test
    public void testRiderAutoOsgi() throws Exception {
    	System.err.println(executeCommand("features:addurl mvn:org.fusesource.examples/rider-auto-common/4.0-SNAPSHOT/xml/features"));
        installAndCheckFeature("rider-auto-osgi");
        unInstallAndCheckFeature("rider-auto-osgi");
    }

    @Configuration
    public Option[] config() {
        return new Option[]{
                esbDistributionConfiguration(), keepRuntimeFolder(),
                editConfigurationFilePut("etc/system.properties", "esb.version", "7.0.0.fuse-061"),
                logLevel(LogLevelOption.LogLevel.INFO)};
    }
    
    /**
     * Returns the Version of Karaf to be used.
     *
     * @return
     */
    protected String getKarafVersion() {
        //TODO: This is a hack because pax-exam-karaf will not work with non numeric characters in the version.
        //We will need to change it once pax-exam-karaf get fixed (version 0.4.0 +).
        return "2.2.5";
    }

    /**
     * Create an {@link org.ops4j.pax.exam.Option} for using a ESB distribution.
     *
     * @return
     */
    protected Option esbDistributionConfiguration() {
        return new DefaultCompositeOption(
                new Option[]{karafDistributionConfiguration().frameworkUrl(
                        maven().groupId(GROUP_ID).artifactId(ARTIFACT_ID).version("7.0.0.fuse-061").type("tar.gz"))
                        .karafVersion(getKarafVersion()).name("Fuse ESB Distro").unpackDirectory(new File("target/paxexam/unpack/")),
                        useOwnExamBundlesStartLevel(50),
                      editConfigurationFilePut("etc/config.properties", "karaf.startlevel.bundle", "50"),
                      mavenBundle("org.fusesource.tooling.testing","pax-exam-karaf", "7.0.0.fuse-061")
                });
    }
}
