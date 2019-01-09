/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.security.utils.testing;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.commons.jackson.CustomSerializationObjectMapper;
import org.craftercms.commons.jackson.ObjectIdDeserializer;
import org.craftercms.commons.jackson.ObjectIdSerializer;
import org.craftercms.commons.rest.HttpMessageConvertingResponseWriter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;

/**
 * Base for REST handler tests.
 *
 * @author avasquez
 */
public abstract class AbstractRestHandlerTestBase {

    protected HttpMessageConvertingResponseWriter createResponseWriter() {
        ContentNegotiationManagerFactoryBean factoryBean = new ContentNegotiationManagerFactoryBean();
        factoryBean.afterPropertiesSet();

        ContentNegotiationManager contentNegotiationManager = factoryBean.getObject();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

        List<JsonSerializer<?>> serializers = new ArrayList<>();
        serializers.add(new ObjectIdSerializer());

        Map<Class<?>, JsonDeserializer<?>> deserializers = new HashMap<>();
        deserializers.put(ObjectId.class, new ObjectIdDeserializer());

        CustomSerializationObjectMapper objectMapper = new CustomSerializationObjectMapper();
        objectMapper.setSerializers(serializers);
        objectMapper.setDeserializers(deserializers);
        objectMapper.init();

        MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonMessageConverter.setObjectMapper(objectMapper);

        messageConverters.add(jsonMessageConverter);

        return new HttpMessageConvertingResponseWriter(contentNegotiationManager, messageConverters);
    }

}
