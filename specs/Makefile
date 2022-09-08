##
## Copyright (C) 2000-2004 Iowa State University
##
## This file is part of JML
##
## This program is free software; you can redistribute it and/or modify
## it under the terms of the GNU General Public License as published by
## the Free Software Foundation; either version 2 of the License, or
## (at your option) any later version.
##
## This program is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with this program; if not, write to the Free Software
## Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
##
## $Id: Makefile,v 1.1 2009/05/13 20:13:57 ddobniewski Exp $
##

## ----------------------------------------------------------------- 
## At the bottom of this file we import $(TOPDIR)/Make.ProjDefs.  See the
## that file for substantial documentation.
## ----------------------------------------------------------------- 

TOPDIR =	..

SUBDIRS = java javax org/multijava/universes/rt

TESTDIRS = java javax org/multijava/universes/rt

## ----------------------------------------------------------------------
## RULES

default all:	runtests

check: check-against-classfiles

ifdef ALLTESTS
runtests-this: check-against-classfiles
endif

# Don't use the -m flag below, because this would cause tests to ignore
# missing methods, which causes user-visible bugs.
.PHONY: check-against-classfiles
check-against-classfiles:
	@echo COMPARING SYSTEM SPECS WITH CLASS FILES
	$(JAVA) org.jmlspecs.jmlspec.Main -Q -i -protected -diff ${PACKAGES}

# If you have servlet.jar file, you can use the following
.PHONY: check-against-classfiles-full
check-against-classfiles-full:
	@echo COMPARING SYSTEM SPECS WITH CLASS FILES
	$(JAVA) org.jmlspecs.jmlspec.Main -Q -i -protected -diff ${PACKAGES} javax.servlet javax.servlet.http


# Generate jmldoc pages for all the specs
specdocs:
	$(JMLDOC) -Q -private -d javadocs -R .

distclean-this: remove-javadocs-dir

remove-javadocs-dir:
	$(DELTREE) javadocs

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.ProjDefs

## Package list for jmldocs and jmlspec comparisons

ifdef ALLTESTS
PACKAGES = `find . -type d | sed -e '1d;/CVS/d;/javadocs/d;s%^./%%;s%/%.%g;/^java$$/d;/^javax$$/d;/^javax.servlet/d;/^org$$/d;/^org.multijava$$/d;/^org.multijava.universes$$/d'`

else
PACKAGES = java.lang java.lang.reflect \
	java.math java.util java.util.regex java.awt.event java.awt.color \
        java.io java.net java.sql java.security java.security.interfaces \
	javax.crypto javax.servlet javax.servlet.http \
	javax.xml.parsers \
	org.multijava.universes.rt
endif
