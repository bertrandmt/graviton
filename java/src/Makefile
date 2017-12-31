src := org/bmt/graviton/Vector.java \
       org/bmt/graviton/MassModel.java \
       org/bmt/graviton/Mass.java      \
       org/bmt/graviton/SpaceModel.java\
       org/bmt/graviton/Space.java     \
       org/bmt/graviton/JGraviton.java \

obj := $(src:%.java=%.class)

all: $(obj)

$(obj): %.class: %.java
	javac $<

run:
	java org.bmt.graviton.JGraviton
