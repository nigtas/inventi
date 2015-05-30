package lt.inventi.playground;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.io.FilenameUtils;

public class PlaygroundRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file://src/main/resources/?noop=true")
                .split(body(String.class).tokenize("\n"), new MyOrderStrategy())
                .to("mock:result")
                .end()
        .to("file://src/main/resources/results");
    }

    public static class MyOrderStrategy implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            //get Index
            int currentNr = Integer.parseInt(newExchange.getProperty("CamelSplitIndex").toString()) + 1;
            Message newIn = newExchange.getIn();
            String newBody = newExchange.getIn().getBody(String.class);
            //check if we have old msg, get old body depending on that (we wont have 1st msg only on first access
            String oldBody = oldExchange==null?"":oldExchange.getIn().getBody(String.class);
            newIn.setBody(oldBody + currentNr + ") " + newBody + "\n");
            String fileName = FilenameUtils.removeExtension(newIn.getHeader("CamelFileName").toString());
            newIn.setHeader(Exchange.FILE_NAME, fileName + "-result.txt");
            return newExchange;
        }
    }
}
