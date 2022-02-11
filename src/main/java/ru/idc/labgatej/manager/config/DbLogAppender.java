package ru.idc.labgatej.manager.config;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Plugin(
    name="DbLogAppender",
    category = Core.CATEGORY_NAME,
    elementType = Appender.ELEMENT_TYPE
)
public class DbLogAppender
extends AbstractAppender
{
    List<String> logList = new ArrayList<>();

    private DbLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    @PluginFactory
    public static DbLogAppender createAppender(
        @PluginAttribute("name") String name,
        @PluginElement("Filter") Filter filter,
        @PluginElement("Layout") Layout layout,
        @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
        @PluginElement("Properties") Property[] properties
    )
    {
        return new DbLogAppender(name, filter, layout, ignoreExceptions, properties);
    }

    @Override
    public void append(LogEvent logEvent) {
        logList.add(logEvent.getMessage().toString());
    }
}
