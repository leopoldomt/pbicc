package icc.visitors;

import japa.parser.ast.expr.Expression;
import japa.parser.ast.type.Type;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;
import java.util.ArrayList;

import java.util.regex.Pattern;

public class ExplicitIntentVisitor extends VoidVisitorAdapter<Object>
{

    private List<String> icc_links;

    public ExplicitIntentVisitor()
    {
        super();

        this.icc_links = new ArrayList<String>();
    }

    public List<String> get_icc_links()
    {
        return this.icc_links;
    }

    @Override
    public void visit(MethodCallExpr n, Object arg)
    {
        super.visit(n, arg);

        if (n != null)
        {
            List<Expression> args = n.getArgs();

            if (args != null)
            {
                // the method being called is 'startActivity'
                if (n.getName().equals("startActivity"))
                {
                    // 1 arg
                    if (args.size() == 1)
                    {
                        Expression clazz = args.get(0);

                        // object creation
                        if (clazz instanceof ObjectCreationExpr)
                        {
                            ObjectCreationExpr newIntent = (ObjectCreationExpr) clazz;

                            // an intent is being created
                            if (newIntent.getType().toString().equals("Intent"))
                            {
                                List<Expression> intent_args = newIntent.getArgs();

                                // two args are given
                                if(intent_args.size() == 2)
                                {
                                    Expression component = intent_args.get(1);

                                    String receiver_class_expr = component.toString();

                                    // the expression ends with '.class'
                                    if (receiver_class_expr.endsWith(".class"))
                                    {
                                        String receiver_class = receiver_class_expr.split(Pattern.quote("."))[0];

                                        // adding this link to the List
                                        this.icc_links.add(receiver_class);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
