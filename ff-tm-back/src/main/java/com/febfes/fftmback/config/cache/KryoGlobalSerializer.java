package com.febfes.fftmback.config.cache;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.dto.ProjectForUserDto;
import com.febfes.fftmback.dto.UserDto;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import lombok.NonNull;
import org.springframework.cache.interceptor.SimpleKey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class KryoGlobalSerializer implements StreamSerializer<Object> {

    private final Kryo kryo = new Kryo();

    public KryoGlobalSerializer() {
        kryo.setRegistrationRequired(true);
        // common
        kryo.register(List.class);
        kryo.register(Object[].class);
        kryo.register(SimpleKey.class);
        kryo.register(LocalDateTime.class);

        kryo.register(ProjectForUserDto.class);
        kryo.register(ProjectDto.class);
        kryo.register(ProjectEntity.class);
        kryo.register(RoleName.class);
        kryo.register(UserDto.class);
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
