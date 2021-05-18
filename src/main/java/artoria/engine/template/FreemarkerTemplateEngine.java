package artoria.engine.template;

import artoria.exception.ExceptionUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;

import static artoria.common.Constants.*;

/**
 * Freemarker template engine.
 * @author Kahle
 */
public class FreemarkerTemplateEngine extends AbstractTemplateEngine {
    private Configuration configuration;

    public FreemarkerTemplateEngine() {
        try {
            Configuration configuration = new Configuration();
            TemplateLoader[] loaders = new TemplateLoader[TWO];
            loaders[ZERO] = new FileTemplateLoader(new File(DOT));
            loaders[ONE] = new ClassTemplateLoader(FreemarkerTemplateEngine.class, SLASH);
            MultiTemplateLoader loader = new MultiTemplateLoader(loaders);
            configuration.setTemplateLoader(loader);
            this.configuration = configuration;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public FreemarkerTemplateEngine(Configuration configuration) {

        this.configuration = configuration;
    }

    @Override
    public void render(String name, String encoding, Object data, Writer output) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        Assert.notNull(output, "Parameter \"output\" must not null. ");
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        if (StringUtils.isBlank(encoding)) { encoding = DEFAULT_ENCODING_NAME; }
        try {
            Template template = configuration.getTemplate(name, encoding);
            template.process(data, output);
        }
        catch (TemplateException e) {
            throw new RenderException(e);
        }
        catch (IOException e) {
            throw new RenderException(e);
        }
    }

    @Override
    public void render(Object data, Writer output, String logTag, Reader reader) {
        Assert.notBlank(logTag, "Parameter \"logTag\" must not blank. ");
        Assert.notNull(reader, "Parameter \"reader\" must not null. ");
        Assert.notNull(output, "Parameter \"output\" must not null. ");
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        try {
            Template template = new Template(logTag, reader, configuration);
            template.process(data, output);
        }
        catch (TemplateException e) {
            throw new RenderException(e);
        }
        catch (IOException e) {
            throw new RenderException(e);
        }
    }

    @Override
    public void render(Object data, Writer output, String logTag, String template) {
        Assert.notBlank(template, "Parameter \"template\" must not blank. ");
        render(data, output, logTag, new StringReader(template));
    }

}
