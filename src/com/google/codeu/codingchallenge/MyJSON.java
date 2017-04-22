// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.codingchallenge;

import java.util.Collection;
import java.util.HashMap;

final class MyJSON implements JSON {

  private HashMap<String, Object> jsonData;

  @Override
  public JSON getObject(String name) {

    if (jsonData==null) {
      //if not yet initialized, do so
      jsonData = new HashMap<String, Object>();
      //and return null because empty map can't contain name
      return null;
    }

    //get value at given name
    Object val = jsonData.get(name);

    if (val instanceof String) {
      //not an OBJECT by that name, return NULL 
      return null;
    } else {
      //either the OBJ by that name, or NULL (key does not exists)
      //typecasting safe because if not a String, must be JSON
      return (JSON)val; 
    }
  }

  @Override
  public JSON setObject(String name, JSON value) {

    //if data not intiialized, do so here
    if (jsonData==null) {
      jsonData = new HashMap<String, Object>();
    }

    //add name:value pair to data
    jsonData.put(name,value);
    return this;
  }

  @Override
  public String getString(String name) {

    if (jsonData==null) {
      //if not yet initialized, do so
      jsonData = new HashMap<String, Object>();
      //and return null because empty map can't contain name
      return null;
    }

    //get value at given name
    Object val = jsonData.get(name);

    if (val instanceof String) {
      //name exists and is the name of a String
      //typecasting safe because type checked by conditional
      return (String)val;
    } else {
      //name doesn't exist or is the name of an Object
      return null; 
    }
  }

  @Override
  public JSON setString(String name, String value) {
    //if data not intiialized, do so here
    if (jsonData==null) {
      jsonData = new HashMap<String, Object>();
    }

    //check dummy case (if both are "" "" -> EMPTY JSON)
    if (!(name.equals("") && value.equals(""))) {
      //add name:value pair to data
      jsonData.put(name,value);
    }

    return this;
  }

  @Override
  public void getObjects(Collection<String> names) {
    for (String name: jsonData.keySet()) {
      if (!(jsonData.get(name) instanceof String)) {
        names.add(name);
      }
    }
  }

  @Override
  public void getStrings(Collection<String> names) {
    for (String name: jsonData.keySet()) {
      if (jsonData.get(name) instanceof String) {
        names.add(name);
      }
    }
  }
}
