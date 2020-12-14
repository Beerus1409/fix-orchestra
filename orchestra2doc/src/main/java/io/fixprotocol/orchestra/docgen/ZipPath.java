/*
 * Copyright 2018 FIX Protocol Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package io.fixprotocol.orchestra.docgen;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * Treats ZipEntry name as a Path, logic missing from the Java library
 * 
 * @author Don Mendelson
 *
 */
class ZipPath implements Path {

  private final static String SEPARATOR = "/";

  public final static ZipPath ROOT = new ZipPath(SEPARATOR);

  private final String str;

  public ZipPath(String str) {
    this.str = Objects.requireNonNull(str);
  }

  @Override
  public int compareTo(Path other) {
    return str.compareTo(other.toString());
  }

  @Override
  public boolean endsWith(Path other) {
    return this.str.endsWith(other.toString());
  }

  @Override
  public boolean endsWith(String other) {
    return this.str.endsWith(other);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final ZipPath other = (ZipPath) obj;
    if (str == null) {
      return other.str == null;
    } else return str.equals(other.str);
  }

  @Override
  public Path getFileName() {
    return new ZipPath(this.str.substring(this.str.lastIndexOf(SEPARATOR) + 1));
  }

  @Override
  public FileSystem getFileSystem() {
    return null;
  }

  @Override
  public Path getName(int index) {
    return new ZipPath(this.str.split(SEPARATOR)[index]);
  }

  public int getNameCount() {
    return this.str.split(SEPARATOR).length;
  }

  @Override
  public Path getParent() {
    if (this.str.equals(SEPARATOR)) {
      return null;
    } else {
      return new ZipPath(this.str.substring(0, this.str.lastIndexOf(SEPARATOR)));
    }
  }

  @Override
  public Path getRoot() {
    if (isAbsolute()) {
      return ROOT;
    } else {
      return null;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((str == null) ? 0 : str.hashCode());
    return result;
  }

  @Override
  public boolean isAbsolute() {
    return startsWith(ROOT);
  }

  @Override
  public Iterator<Path> iterator() {
    final String[] names = this.str.split(SEPARATOR);
    return new Iterator<Path>() {
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < names.length;
      }

      @Override
      public Path next() {
        index++;
        return new ZipPath(names[index - 1]);
      }

    };
  }

  @Override
  public Path normalize() {
    return this;
  }

  @Override
  public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers)
      throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path relativize(Path other) {
    final String otherStr = other.toString();
    final int beginIndex = otherStr.indexOf(this.str);
    if (beginIndex == -1) {
      return null;
    } else {
      return new ZipPath(otherStr.substring(this.str.length()+1));
    }
  }

  @Override
  public Path resolve(Path other) {
    if (other.isAbsolute()) {
      return other;
    } else if (other.toString().isEmpty()) {
      return this;
    } else if (this.str.equals(SEPARATOR)) {
      return new ZipPath(this.str + other.toString());
    } else {
      return new ZipPath(String.join(SEPARATOR, this.str, other.toString()));
    }
  }

  @Override
  public Path resolve(String other) {
    return resolve(new ZipPath(other));
  }

  @Override
  public Path resolveSibling(Path other) {
    final Path parent = getParent();
    if (parent == null || other.isAbsolute()) {
      return other;
    } else if (other.toString().isEmpty()) {
      return parent;
    } else {
      return new ZipPath(parent.toString()+other.toString());
    }
  }

  @Override
  public Path resolveSibling(String other) {
    return resolveSibling(new ZipPath(other));
  }

  @Override
  public boolean startsWith(Path other) {
    return this.str.startsWith(other.toString());
  }

  @Override
  public boolean startsWith(String other) {
    return this.str.startsWith(other);
  }

  @Override
  public Path subpath(int beginIndex, int endIndex) {
    final String[] names = this.str.split(SEPARATOR);
    return new ZipPath(String.join(SEPARATOR, Arrays.copyOfRange(names, beginIndex, endIndex)));
  }

  @Override
  public Path toAbsolutePath() {
    if (isAbsolute()) {
      return this;
    } else {
      return null;
    }
  }

  @Override
  public File toFile() {
    return new File(this.toString());
  }

  @Override
  public Path toRealPath(LinkOption... options) throws IOException {
    return this;
  }

  @Override
  public String toString() {
    return str;
  }

  @Override
  public URI toUri() {
    throw new UnsupportedOperationException();
  }

}
