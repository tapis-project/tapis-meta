package edu.utexas.tacc.tapis.meta.parameters;

public class MetaEnv {
  
  public enum EnvVar {
    META_CORE_SERVER();
  
    private final String _envName;
  
    EnvVar() {
      this._envName = "meta.core.server";
    }
  
    public String getEnvName() {
      return this._envName;
    }
  }
  
}
