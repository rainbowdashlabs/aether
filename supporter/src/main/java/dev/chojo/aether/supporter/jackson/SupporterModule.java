package dev.chojo.aether.supporter.jackson;

import dev.chojo.aether.supporter.configuration.modules.subscriptions.SubscriptionKey;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType;
import dev.chojo.aether.supporter.registry.Key;
import dev.chojo.aether.supporter.registry.Registry;
import dev.chojo.aether.supporter.registry.SupporterRegistry;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SupporterModule extends SimpleModule {
    public SupporterModule() {
        super("SupporterModule");

        register(Platform.class, SupporterRegistry.PLATFORMS);
        register(SubscriptionKey.class, SupporterRegistry.SUBSCRIPTION_TYPES);
        register(PurchaseType.class, SupporterRegistry.PURCHASE_TYPE);
    }

    private <T extends Key> void register(Class<T> clazz, Registry<T> registry) {
        addSerializer(clazz, new KeySerializer<>(clazz));
        addDeserializer(clazz, new KeyDeserializerImpl<>(clazz, registry));
        addKeySerializer(clazz, new KeySerializer<>(clazz));
        addKeyDeserializer(clazz, new KeyKeyDeserializer<>(registry));
    }

    private static class KeySerializer<T extends Key> extends StdSerializer<T> {
        protected KeySerializer(Class<T> t) {
            super(t);
        }

        @Override
        public void serialize(T value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
            gen.writeString(value.name());
        }
    }

    private static class KeyDeserializerImpl<T extends Key> extends StdDeserializer<T> {
        private final Registry<T> registry;

        protected KeyDeserializerImpl(Class<T> vc, Registry<T> registry) {
            super(vc);
            this.registry = registry;
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            try {
                String name = p.getText();
                return registry.byName(name).orElseThrow(() -> new IOException("Unknown key: " + name));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class KeyKeyDeserializer<T extends Key> extends KeyDeserializer {
        private final Registry<T> registry;

        private KeyKeyDeserializer(Registry<T> registry) {
            this.registry = registry;
        }

        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) throws JacksonException {
            return registry.byName(key).orElseThrow(() -> new RuntimeException("Unknown key: " + key));
        }
    }
}
