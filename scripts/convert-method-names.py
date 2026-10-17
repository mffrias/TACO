#!/usr/bin/env python3
"""Renames the methods that TACO drivers check to the naming TACO expects:
<package>_<Class>_<method>(<type>, <type>), e.g.
  check(GENERIC_PROPERTIES, "contains_0", true)
  check(GENERIC_PROPERTIES, "contains(java.lang.Object)", true)
both become
  check(GENERIC_PROPERTIES, "icse_singlylinkedlist_set1_SinglyLinkedList_contains(java_lang_Object)", true)
for a driver whose getClassToCheck() returns "icse.singlylinkedlist.set1.SinglyLinkedList".

The parameter types are read from the declaration of the method in the class
under analysis (tests/<package>/<Class>.java) and written as TACO compares them
(see src/ar/edu/taco/stryker/MethodToCheck.java): fully qualified, dots as
underscores, primitive types as they are. Only the string literal changes; the
rest of each file is kept byte for byte. Calls that are already in this form are
left alone. A call is not converted, and is reported, when the class or the
method is not found, when the method is overloaded, or when a type cannot be
resolved.

Usage: scripts/convert-method-names.py [--all] [--dry-run] [DRIVER.java ...]
  without files: the drivers under unittest/ that run Stryker
                 (setConfigKeyAttemptToCorrectBug(true)); with --all, every driver
  --dry-run      report what would change, change nothing
Exit status: 0 if every call was converted or already in the new form, 1 otherwise.
"""
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SOURCE_ROOT = os.path.join(ROOT, "tests")
PRIMITIVES = {"boolean", "byte", "char", "short", "int", "long", "float", "double"}
JAVA_LANG = {"Object", "String", "Integer", "Long", "Short", "Byte", "Character", "Boolean", "Float",
             "Double", "Number", "Comparable", "Iterable", "Exception", "RuntimeException", "Error",
             "Throwable", "Class", "StringBuffer", "StringBuilder", "CharSequence", "Math", "System"}

CLASS_TO_CHECK = re.compile(r'getClassToCheck\s*\(\s*\)\s*\{\s*return\s*"([^"]+)"\s*;', re.S)
CHECK_CALL = re.compile(r'(check\(\s*[A-Za-z_][A-Za-z0-9_]*\s*,\s*")([^"]*)(")')
OLD_SUFFIX = re.compile(r'^(.*)_([0-9]+)$')


def strip_comments(source):
    """Removes /* */ comments (and so JML annotations such as /*@ nullable @*/) and // comments."""
    source = re.sub(r'/\*.*?\*/', ' ', source, flags=re.S)
    return re.sub(r'//[^\n]*', ' ', source)


class Subject(object):
    """The class under analysis, read from its source file."""

    def __init__(self, qualified_name):
        self.qualified_name = qualified_name
        self.package = qualified_name.rsplit('.', 1)[0] if '.' in qualified_name else ""
        self.path = os.path.join(SOURCE_ROOT, *qualified_name.split('.')) + ".java"
        if not os.path.isfile(self.path):
            raise LookupError("the class %s has no source file %s"
                              % (qualified_name, os.path.relpath(self.path, ROOT)))
        with open(self.path, encoding="utf-8", errors="replace") as f:
            self.code = strip_comments(f.read())
        self.single_imports = {}
        self.wildcard_imports = []
        for imported in re.findall(r'^\s*import\s+([A-Za-z0-9_.]+(?:\.\*)?)\s*;', self.code, re.M):
            if imported.endswith(".*"):
                self.wildcard_imports.append(imported[:-2])
            else:
                self.single_imports[imported.rsplit('.', 1)[-1]] = imported

    def declarations(self, method):
        """The parameter lists of the declarations of `method`, as written."""
        pattern = re.compile(r'(?:^|[\s;{}])(?:[A-Za-z_][\w.<>\[\], ]*\s+)' + re.escape(method)
                             + r'\s*\(([^()]*)\)\s*(?:throws\s+[\w.,\s]+)?\{')
        return [m.group(1) for m in pattern.finditer(self.code)]

    def resolve(self, type_name):
        """The fully qualified name of a type as written in this class, or None."""
        dims = ""
        while type_name.endswith("[]"):
            type_name, dims = type_name[:-2].strip(), dims + "[]"
        if type_name in PRIMITIVES:
            return type_name + dims
        if '.' in type_name:
            return type_name + dims
        if type_name in self.single_imports:
            return self.single_imports[type_name] + dims
        candidates = [self.package] + self.wildcard_imports
        for package in candidates:
            path = os.path.join(SOURCE_ROOT, *(package.split('.') if package else [])) + os.sep + type_name + ".java"
            if os.path.isfile(path):
                return (package + "." if package else "") + type_name + dims
        if type_name in JAVA_LANG:
            return "java.lang." + type_name + dims
        return None


def parameter_types(subject, method):
    declarations = subject.declarations(method)
    if not declarations:
        raise LookupError("no method %s in %s" % (method, os.path.relpath(subject.path, ROOT)))
    if len(declarations) > 1:
        raise LookupError("%s is overloaded in %s" % (method, os.path.relpath(subject.path, ROOT)))
    types = []
    for parameter in declarations[0].split(','):
        parameter = re.sub(r'\b(final)\b', ' ', parameter).strip()
        if not parameter:
            continue
        type_name = re.sub(r'\s*\[\s*\]', '[]', parameter.rsplit(None, 1)[0]).replace(' ', '')
        resolved = subject.resolve(type_name)
        if resolved is None:
            raise LookupError("cannot resolve the type %s of %s in %s"
                              % (type_name, method, os.path.relpath(subject.path, ROOT)))
        types.append(resolved)
    return types


def new_name(class_to_check, spec):
    prefix = class_to_check.replace('.', '_') + "_"
    if spec.startswith(prefix) and '(' in spec:
        return None  # already converted
    method = spec.split('(')[0].strip()
    if '(' not in spec:
        old = OLD_SUFFIX.match(method)
        if old:
            method = old.group(1)
    types = parameter_types(Subject(class_to_check), method)
    return prefix + method + "(" + ", ".join(t.replace('.', '_') for t in types) + ")"


def drivers(run_all):
    found = []
    for directory, _, names in os.walk(os.path.join(ROOT, "unittest")):
        for name in sorted(names):
            if name.endswith(".java"):
                path = os.path.join(directory, name)
                with open(path, encoding="utf-8", errors="replace") as f:
                    text = f.read()
                if "check(" in text and (run_all or "setConfigKeyAttemptToCorrectBug(true)" in text):
                    found.append(path)
    return sorted(found)


def main(argv):
    run_all = "--all" in argv
    dry_run = "--dry-run" in argv
    files = [a for a in argv if not a.startswith("--")] or drivers(run_all)
    converted = unchanged = failed = 0
    for path in files:
        with open(path, "rb") as f:
            raw = f.read()
        text = raw.decode("utf-8", errors="surrogateescape")
        match = CLASS_TO_CHECK.search(text)
        if not match:
            print("SKIPPED %s: no getClassToCheck() returning a literal" % os.path.relpath(path, ROOT))
            failed += 1
            continue
        class_to_check = match.group(1)
        problems = []

        def replace(call):
            nonlocal converted, unchanged
            try:
                name = new_name(class_to_check, call.group(2))
            except LookupError as e:
                problems.append("%s: %s" % (call.group(2), e))
                return call.group(0)
            if name is None:
                unchanged += 1
                return call.group(0)
            converted += 1
            return call.group(1) + name + call.group(3)

        new_text = CHECK_CALL.sub(replace, text)
        for problem in problems:
            print("NOT CONVERTED %s: %s" % (os.path.relpath(path, ROOT), problem))
        failed += len(problems)
        if new_text != text and not dry_run:
            with open(path, "wb") as f:
                f.write(new_text.encode("utf-8", errors="surrogateescape"))
    print("%d calls converted, %d already in the new form, %d not converted (%d files)"
          % (converted, unchanged, failed, len(files)))
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
