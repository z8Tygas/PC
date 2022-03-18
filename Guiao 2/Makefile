FLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

# This uses the line continuation character (\) for readability
# You can list these all on a single line, separated by a space instead.
# If your version of make can't handle the leading tabs on each
# line, just remove them (these are also just added for readability).
CLASSES = \
	ex1.java \
	ex2.java  \

default: classes

classes: $(CLASSES:.java=.class)

ex1: $(ex1.java)

ex2: $(ex2.java)

clean:
	$(RM) *.class