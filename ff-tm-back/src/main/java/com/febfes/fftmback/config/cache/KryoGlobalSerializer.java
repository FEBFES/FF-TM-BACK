package com.febfes.fftmback.config.cache;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import lombok.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class KryoGlobalSerializer implements StreamSerializer<Object> {

    private final Kryo kryo = new Kryo();

    public KryoGlobalSerializer() {
        kryo.setRegistrationRequired(true);
        kryo.register(RoleEntity.class);
        kryo.register(ProjectEntity.class);
        kryo.register(RoleName.class);
        kryo.register(LocalDateTime.class);
    }

    @Override
    public void write(ObjectDataOutput out, @NonNull Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeClassAndObject(output, object);
        output.close();
        byte[] bytes = baos.toByteArray();
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    @Override
    public @NonNull Object read(ObjectDataInput in) throws IOException {
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        Input input = new Input(new ByteArrayInputStream(bytes));
        Object obj = kryo.readClassAndObject(input);
        input.close();
        return obj;
    }

    @Override
    public int getTypeId() {
        return 1;
    }

    @Override
    public void destroy() {
    }
}
