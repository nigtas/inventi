package lt.inventi.playground;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.FilenameUtils;


public class PlaygroundRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file://src/main/resources/?move=.done")
                .choice()
                    .when(body().isNotEqualTo(""))
                        .process(new Processor() {
                            public void process(Exchange msg) {
                                String text = msg.getIn().getBody(String.class).toLowerCase()+ "\nEdited by Vyke";
                                String fileName = FilenameUtils.removeExtension(msg.getIn().getHeader("CamelFileName").toString());
                                msg.getOut().setBody(text);
                                msg.getOut().setHeader(Exchange.FILE_NAME, fileName + "-result.txt");
                            }
                        })
                            .to("file://src/main/resources/results")
                .endChoice();
    }
}
