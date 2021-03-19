import java.io.IOException;

import dev.simbiot.Component;
import dev.simbiot.MapProps;
import dev.simbiot.Props;
import dev.simbiot.Runtime;
import dev.simbiot.Slots;
import dev.simbiot.StreamWriter;
import dev.simbiot.Writer;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Test {

    public static void main(String[] args) throws IOException {
//        final SvelteParser parser = new SvelteParser();
//        final Program program = parser.parse(new FileInputStream(new File("test.json")));
//        final Compiler compiler = new Compiler();
//
//        compiler.compile("Test", program);

        final TestComponent component = new TestComponent();

        MapProps props = new MapProps();
        props.set("name", "Vadim");
        props.set("link", "https://tt.me/vadim");
//        props.set("access", true);

        component.render(new StreamWriter(System.out), props, new Slots());
    }


    public static class TestComponent implements Component {
        private static final byte[][] $$PARTS = new byte[][]{"<main>".getBytes(), "Access denied".getBytes(), "Hello <a href=\"".getBytes(), "\">".getBytes(), "</a>".getBytes(), "</main>".getBytes()};

        public void render(Writer var1, Props var2, Slots var3) throws IOException {
            Object var4 = var2.get("name", "Vadim");
            Object var5 = var2.get("link");
            Object var6 = var2.get("access");
            var1.write($$PARTS[0]);
            if (Runtime.toBoolean(var6)) {
                var1.write($$PARTS[1]);
            } else {
                var1.write($$PARTS[2]);
                var1.write(var5);
                var1.write($$PARTS[3]);
                var1.write(var4);
                var1.write($$PARTS[4]);
            }

            var1.write($$PARTS[5]);
        }
    }

}
