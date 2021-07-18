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
public class FreemarkerTemplateEngine extends AbstractRichTemplateEngine {
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
    public void render(Object data, Writer writer, String tag, Reader reader) {
        Assert.notBlank(tag, "Parameter \"tag\" must not blank. ");
        Assert.notNull(reader, "Parameter \"reader\" must not null. ");
        Assert.notNull(writer, "Parameter \"writer\" must not null. ");
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        try {
            Template template = new Template(tag, reader, configuration);
            template.process(data, writer);
        }
        catch (TemplateException e) {
            throw new RenderException(e);
        }
        catch (IOException e) {
            throw new RenderException(e);
        }
    }

    @Override
    public void render(Object data, Writer writer, String tag, String template) {
        Assert.notBlank(template, "Parameter \"template\" must not blank. ");
        render(data, writer, tag, new StringReader(template));
    }

    @Override
    public void render(String name, String encoding, Object data, Writer writer) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        Assert.notNull(writer, "Parameter \"writer\" must not null. ");
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        if (StringUtils.isBlank(encoding)) { encoding = DEFAULT_ENCODING_NAME; }
        try {
            Template template = configuration.getTemplate(name, encoding);
            template.process(data, writer);
        }
        catch (TemplateException e) {
            throw new RenderException(e);
        }
        catch (IOException e) {
            throw new RenderException(e);
        }
    }

}
