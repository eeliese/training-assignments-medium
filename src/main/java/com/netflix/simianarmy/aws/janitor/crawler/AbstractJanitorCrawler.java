package com.netflix.simianarmy.aws.janitor.crawler;

import com.netflix.simianarmy.Resource;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.netflix.simianarmy.aws.janitor.RDSJanitorResourceTracker.LOGGER;

public abstract class AbstractJanitorCrawler {

    public AbstractJanitorCrawler() {
    }

    public static void setReferencedASGCount (Map<String, List<String>> elbtoASGMap, List<Resource> resources){
        for(Resource resource : resources) {
            List<String> asgList = elbtoASGMap.get(resource.getId());
            if (asgList != null && asgList.size() > 0) {
                resource.setAdditionalField("referencedASGCount", "" + asgList.size());
                String asgStr = StringUtils.join(asgList,",");
                resource.setDescription(resource.getDescription() + ", ASGS=" + asgStr);
                LOGGER.debug(String.format("Resource ELB %s is referenced by ASGs %s", resource.getId(), asgStr));
            } else {
                resource.setAdditionalField("referencedASGCount", "0");
                resource.setDescription(resource.getDescription() + ", ASGS=none");
                LOGGER.debug(String.format("No ASGs found for ELB %s", resource.getId()));
            }
        }
    }

    public static void setTagsJsonNode(JsonNode tags, Resource resource){
        if (tags == null || !tags.isArray() || tags.size() == 0) {
            LOGGER.debug(String.format("No tags is found for %s", resource.getId()));
        } else {
            for (Iterator<JsonNode> it = tags.getElements(); it.hasNext();) {
                JsonNode tag = it.next();
                String key = tag.get("key").getTextValue();
                String value = tag.get("value").getTextValue();
                resource.setTag(key, value);
            }
        }
    }


}
