package vistager.mapper;

public interface GenericDtoMapper<REQUEST, RESPONSE, MODEL> {
    MODEL toModel(REQUEST dto);

    RESPONSE toDto(MODEL model);
}
