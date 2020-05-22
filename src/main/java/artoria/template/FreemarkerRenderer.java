package artoria.template;

import artoria.exception.ExceptionUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import static artoria.common.Constants.*;

/**
 * Freemarker template renderer.
 * @author Kahle
 */
public class FreemarkerRenderer implements Renderer {
    private Configuration configuration;

    public FreemarkerRenderer() {
        try {
            Configuration configuration = new Configuration();
            TemplateLoader[] loaders = new TemplateLoader[TWO];
            loaders[ZERO] = new FileTemplateLoader(new File(DOT));
            loaders[ONE] = new ClassTemplateLoader(FreemarkerRenderer.class, SLASH);
            MultiTemplateLoader loader = new MultiTemplateLoader(loaders);
            configuration.setTemplateLoader(loader);
            this.configuration = configuration;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public FreemarkerRenderer(Configuration configuration) {

        this.configuration = configuration;
    }

    @Override
    public void render(Object data, Object output, String name, Object input, String charsetName) {
        try {
            Assert.notBlank(name, "Parameter \"name\" must not blank. ");
            Assert.state((output instanceof Writer)
                    , "Parameter \"output\" must instance of \"Writer\". ");
            if ((input instanceof Reader) || (input instanceof String)) {
                Reader reader = input instanceof Reader
                        ? (Reader) input : new StringReader((String) input);
                Template template = new Template(name, reader, this.configuration);
                template.process(data, (Writer) output);
            }
            else {
                charsetName = StringUtils.isNotBlank(charsetName)
                        ? charsetName : DEFAULT_CHARSET_NAME;
                Template template = this.configuration.getTemplate(name, charsetName);
                template.process(data, (Writer) output);
            }
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
