/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Contributor(s):
 *   Norris Boyd
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License Version 2 or later (the "GPL"), in which
 * case the provisions of the GPL are applicable instead of those above. If
 * you wish to allow use of your version of this file only under the terms of
 * the GPL and not to allow others to use your version of this file under the
 * MPL, indicate your decision by deleting the provisions above and replacing
 * them with the notice and other provisions required by the GPL. If you do
 * not delete the provisions above, a recipient may use your version of this
 * file under either the MPL or the GPL.
 *
 * ***** END LICENSE BLOCK ***** */

package rhinotest3.org.mozilla.javascript;

/**
 * This class implements generator objects. See 
 * http://developer.mozilla.org/en/docs/New_in_JavaScript_1.7#Generators
 *
 * @author Norris Boyd
 */
public final class NativeGenerator extends IdScriptableObject {
    private static final Object GENERATOR_TAG = new Object();

    static NativeGenerator init(ScriptableObject scope, boolean sealed) {
        // Generator
        // Can't use "NativeGenerator().exportAsJSClass" since we don't want
        // to define "Generator" as a constructor in the top-level scope.
      
        NativeGenerator prototype = new NativeGenerator();
        if (scope != null) {
            prototype.setParentScope(scope);
            prototype.setPrototype(getObjectPrototype(scope));
        }
        prototype.activatePrototypeMap(MAX_PROTOTYPE_ID);
        if (sealed) {
            prototype.sealObject();
        }
        
        // Need to access Generator prototype when constructing
        // Generator instances, but don't have a generator constructor
        // to use to find the prototype. Use the "associateValue" 
        // approach instead.
        if (scope != null) {
            scope.associateValue(GENERATOR_TAG, prototype);
        }
        
        return prototype;
    }
    
    /**
     * Only for constructing the prototype object.
     */
    private NativeGenerator() { }

    public NativeGenerator(Scriptable scope, NativeFunction function,
                           Object savedState)
    {
        this.function = function;
        this.savedState = savedState;
        // Set parent and prototype properties. Since we don't have a
        // "Generator" constructor in the top scope, we stash the
        // prototype in the top scope's associated value.
        Scriptable top = ScriptableObject.getTopLevelScope(scope);
        this.setParentScope(top);
        NativeGenerator prototype = (NativeGenerator)
            ScriptableObject.getTopScopeValue(top, GENERATOR_TAG);
        this.setPrototype(prototype);
    }

    public static final int GENERATOR_SEND  = 0,
                            GENERATOR_THROW = 1,
                            GENERATOR_CLOSE = 2;

    public String getClassName() {
        return "Generator";
    }

    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
          case Id_close:          arity=1; s="close";          break;
          case Id_next:           arity=1; s="next";           break;
          case Id_send:           arity=0; s="send";           break;
          case Id_throw:          arity=0; s="throw";          break;
          case Id___iterator__:   arity=1; s="__iterator__";   break;
          default: throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(GENERATOR_TAG, id, s, arity);
    }

    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
                             Scriptable thisObj, Object[] args)
    {
        if (!f.hasTag(GENERATOR_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();

        if (!(thisObj instanceof NativeGenerator))
            throw incompatibleCallError(f);
        
        NativeGenerator generator = (NativeGenerator) thisObj;
        
        switch (id) {
            
          case Id_close:
            // need to run any pending finally clauses
	        return generator.resume(cx, scope, GENERATOR_CLOSE,
	          		                new GeneratorClosedException());

          case Id_next: 
            // arguments to next() are ignored
            generator.firstTime = false;
            return generator.resume(cx, scope, GENERATOR_SEND,
            		                Undefined.instance);

          case Id_send: {
            Object arg = args.length > 0 ? args[0] : Undefined.instance;
            if (generator.firstTime && !arg.equals(Undefined.instance)) {
                throw ScriptRuntime.typeError0("msg.send.newborn");
            }
            return generator.resume(cx, scope, GENERATOR_SEND, arg);
          }

          case Id_throw:
            return generator.resume(cx, scope, GENERATOR_THROW,
            		args.length > 0 ? args[0] : Undefined.instance);

          case Id___iterator__:
            return thisObj;

          default: 
        	throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    private Object resume(Context cx, Scriptable scope, int operation,
    		              Object value)
    {
        if (savedState == null) {
            if (operation == GENERATOR_CLOSE)
                return Undefined.instance;
            Object thrown;
            if (operation == GENERATOR_THROW) {
                thrown = value;
            } else {
                thrown = NativeIterator.getStopIterationObject(scope);
            }
            throw new JavaScriptException(thrown, lineSource, lineNumber);
        }
        try {
            synchronized (this) {
              // generator execution is necessarily single-threaded and
              // non-reentrant.
              // See https://bugzilla.mozilla.org/show_bug.cgi?id=349263
              if (locked)
                  throw ScriptRuntime.typeError0("msg.already.exec.gen");
              locked = true;
            }
            return function.resumeGenerator(cx, scope, operation, savedState,
                                            value);
    	} catch (GeneratorClosedException e) {
            // On closing a generator in the compile path, the generator
            // throws a special execption. This ensures execution of all pending
            // finalisers and will not get caught by user code. 
            return Undefined.instance;
        } catch (RhinoException e) {
    		lineNumber = e.lineNumber();
    		lineSource = e.lineSource();
    		savedState = null;
    		throw e;
    	} finally {
            synchronized (this) {
              locked = false;
            }
    		if (operation == GENERATOR_CLOSE)
    			savedState = null;
    	}
    }

// #string_id_map#

    protected int findPrototypeId(String s) {
        int id;
// #generated# Last update: 2007-06-14 13:13:03 EDT
        L0: { id = 0; String X = null; int c;
            int s_length = s.length();
            if (s_length==4) {
                c=s.charAt(0);
                if (c=='n') { X="next";id=Id_next; }
                else if (c=='s') { X="send";id=Id_send; }
            }
            else if (s_length==5) {
                c=s.charAt(0);
                if (c=='c') { X="close";id=Id_close; }
                else if (c=='t') { X="throw";id=Id_throw; }
            }
            else if (s_length==12) { X="__iterator__";id=Id___iterator__; }
            if (X!=null && X!=s && !X.equals(s)) id = 0;
            break L0;
        }
// #/generated#
        return id;
    }

    private static final int
        Id_close                 = 1,
        Id_next                  = 2,
        Id_send                  = 3,
        Id_throw                 = 4,
        Id___iterator__          = 5,
        MAX_PROTOTYPE_ID         = 5;

// #/string_id_map#
    private NativeFunction function;
    private Object savedState;
    private String lineSource;
    private int lineNumber;
    private boolean firstTime = true;
    private boolean locked;

    public static class GeneratorClosedException extends RuntimeException {
    }
}


