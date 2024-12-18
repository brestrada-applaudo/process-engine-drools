package com.example.pr.drools;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;


@Service
public class InternalKieService {

  private final KieServices kieServices;
  private final KieHelper kieHelper;
  private final Set<String> procedures;

  public InternalKieService(KieServices kieServices) {
    this.kieServices = kieServices;
    this.kieHelper = new KieHelper();
    this.procedures = new HashSet<>();
  }

  public void AddRule(String procedure, FileSystemResource rule) throws IOException {
    if(!procedures.contains(procedure)) {
      byte[] b1 = rule.getContentAsByteArray();
      Resource resource1 = kieServices.getResources().newByteArrayResource(b1);
      kieHelper.addResource(resource1, ResourceType.DRL);
      procedures.add(procedure);
    }
  }

  public KieSession generateSession() {
    KieBase kieBase = kieHelper.build();
    return kieBase.newKieSession();
  }
}
