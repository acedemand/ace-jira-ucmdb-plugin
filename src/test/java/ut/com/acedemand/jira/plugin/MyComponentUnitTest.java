package ut.com.acedemand.jira.plugin;

import org.junit.Test;
import com.acedemand.jira.plugin.api.MyPluginComponent;
import com.acedemand.jira.plugin.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}