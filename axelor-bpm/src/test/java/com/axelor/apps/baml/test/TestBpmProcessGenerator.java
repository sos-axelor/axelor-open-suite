package com.axelor.apps.baml.test;

import com.axelor.apps.bpm.process.service.BpmProcessTransformer;
import com.axelor.common.ResourceUtils;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;

class TestBpmProcessGenerator {

  @Test
  void test() throws JAXBException {

    InputStream inputStream = ResourceUtils.getResourceStream("bpm-opportunity.xml");

    Reader targetReader = new InputStreamReader(inputStream);

    BpmProcessTransformer bpmTransformer = new BpmProcessTransformer();
    
    String outText = bpmTransformer.transform(targetReader);
    System.err.println(outText);
  }
}
