# -*- makefile -*- mode for emacs
# @(#) $Id: Makefile,v 1.1 2009/05/13 20:13:54 ddobniewski Exp $

# Copyright (C) 1998, 1999 Iowa State University

# This file is part of JML

# JML is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2, or (at your option)
# any later version.

# JML is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with JML; see the file COPYING.  If not, write to
# the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

#
# Developer's Makefile for testing JML specs
#

TOPDIR = ../../..

TESTDIRS = http

PACKAGE = javax.servlet

SPECFILES = 

RAC_CLASSES_JTOP = $(PROJ_JAVAC_CLASSROOT)$(JFILESEP)raccompiled
RAC_CLASSES_DIR = $(TOPDIR)/raccompiled/$(subst .,/,$(PACKAGE))

default all: check

check: checkallspecs compile

# check all JML specs.
checkallspecs:
	$(JML) $(JMLFLAGS) -R .

## build
build-this: raccompile

## runtests
runtests-this: runtests-local

runtests-local:
	-@if test -f skip; \
	  then \
	    for f in *.*j* *.spec ; \
	    do \
		grep -q $${f} skip || $(JML) $(JMLFLAGS) $${f} ; \
	    done; \
	  else \
	       $(JML) $(JMLFLAGS) . ; \
	  fi

# Clean up stuff
#
clean-this : cleanlocal

clean-classes-this: PROJ_CLASSROOT=$(JML_CLASSROOT)/specs

cleanlocal: 
	$(RM) *.*-ckd core *.out

# include standard project targets

include $(TOPDIR)/Make.ProjDefs

