package com.beans.my.feedflow.job.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beans.my.feedflow.base.annotation.Step;
import com.beans.my.feedflow.base.annotation.StepConfig;
import com.beans.my.feedflow.base.controller.BaseAPI;
import com.beans.my.feedflow.base.model.JobStepResponse;
import com.beans.my.feedflow.base.model.StepConfigResponse;
import com.beans.my.feedflow.base.util.Response;
import com.beans.my.feedflow.job.scheduled.JobStep;

@RestController
@RequestMapping("/api/jobStep")
public class JobStepAPI extends BaseAPI{
	
	@Autowired
	protected ApplicationContext applicationContext;
	
	@RequestMapping(value="/list", method = RequestMethod.GET)
	@ResponseBody
	public Response<List<JobStepResponse>> list() throws Exception {
		List<JobStepResponse> list = new ArrayList<JobStepResponse>();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
				false);
		scanner.addIncludeFilter(new AssignableTypeFilter(JobStep.class));
		scanner.addIncludeFilter(new AnnotationTypeFilter(Step.class, true,
				false));
		Set<BeanDefinition> beans = scanner
				.findCandidateComponents("com.beans.my.feedflow");
		for (BeanDefinition t : beans) {
			String clazzName = t.getBeanClassName();
			Class<?> clazz = Class.forName(clazzName, false, this.getClass()
					.getClassLoader());

			Step step = clazz.getAnnotation(Step.class);

			if (step != null) {
				StepConfig[] configValues = step.config();
				LinkedList<StepConfigResponse> config = new LinkedList<StepConfigResponse>();
				for (StepConfig configValue : configValues) {
					config.add(new StepConfigResponse(configValue));
				}
				list.add(new JobStepResponse(t.getBeanClassName(),
						step.value(), config));
			}

		}
		Collections.sort(list, (o1, o2) -> {
			return o1.getLabel().compareTo(o2.getLabel());
		});
		return SUCCESS(list);
	}
}
