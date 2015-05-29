package lt.inventi.playground;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaygroundRouteBuilder extends RouteBuilder {
    static Logger LOG = LoggerFactory.getLogger(PlaygroundRouteBuilder.class);
    @Override
    public void configure() throws Exception {
        from("timer://timer1?period=2000").
        process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println("Hello world!");
            }
        });
        //Bean:
//        from("timer:timer?period=2000").bean(PlaygroundRouteBuilder.class, "printHelloWorld");
    }

    public void  printHelloWorld() {
        LOG.info("Hello world!");
    }
}
