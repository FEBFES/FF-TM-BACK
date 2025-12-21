package com.febfes.fftmback.config.cache;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.RoleEntity;
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

public class KryoSerializer<T> implements StreamSerializer<T> {

    private final Kryo kryo = new Kryo();
    private final Class<T> type;
    private final int typeId;

    public KryoSerializer(Class<T> type, int typeId) {
        this.type = type;
        this.typeId = typeId;

        kryo.setRegistrationRequired(true);
        // common
        kryo.register(List.class);
        kryo.register(Object[].class);
        kryo.register(SimpleKey.class);
        kryo.register(LocalDateTime.class);
        // custom
        kryo.register(ProjectForUserDto.class);
        kryo.register(ProjectDto.class);
        kryo.register(ProjectEntity.class);
        kryo.register(RoleEntity.class);
        kryo.register(RoleName.class);
        kryo.register(UserDto.class);
    }

    @Override
    public void write(ObjectDataOutput out, @NonNull T object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeObject(output, object);
        output.close();

        byte[] bytes = baos.toByteArray();
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    @Override
    public @NonNull T read(ObjectDataInput in) throws IOException {
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readFully(bytes);

        Input input = new Input(new ByteArrayInputStream(bytes));
        T obj = kryo.readObject(input, type);
        input.close();
        return obj;
    }

    @Override
    public int getTypeId() {
        return typeId;
    }

    @Override
    public void destroy() {
    }
}
