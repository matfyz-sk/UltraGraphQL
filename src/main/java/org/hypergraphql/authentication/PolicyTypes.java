package org.hypergraphql.authentication;


/**
 * Hierarchy of policy rules.
 * SUPERADMIN > AUTHOR > SPECIFIC RULE > REGISTER > all others.
 * If AUTHOR is selected in policy rule, then SUPERADMIN can also change the attributes.
 * If only SUPERADMIN is selected in the policy rules, then even author cannot change its own attributes.
 */
public final class PolicyTypes {


    public final static String AUTHOR = "author";
    public final static String SUPERADMIN = "superAdmin";
    public final static String REGISTER = "register";


}
