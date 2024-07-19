package com.example.Spring_Quart_Scheduler_Using_Cron.mapper;

import com.example.Spring_Quart_Scheduler_Using_Cron.dto.EmailRequest;
import com.example.Spring_Quart_Scheduler_Using_Cron.dto.EmailResponse;
import com.example.Spring_Quart_Scheduler_Using_Cron.entity.EmailSchedulerJobInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface EmailMapper {
  EmailMapper Mapper = Mappers.getMapper(EmailMapper.class);

  EmailSchedulerJobInfo toModel(EmailRequest emailRequest);

  EmailResponse toResponse(EmailSchedulerJobInfo emailSchedulerJobInfo);

}
