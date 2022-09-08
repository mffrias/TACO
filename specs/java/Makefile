# -*- makefile -*- mode for emacs
# @(#) $Id: Makefile,v 1.1 2009/05/13 20:13:55 ddobniewski Exp $

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

TOPDIR = ../..

# subdirs must list directories with interfaces (.spec files)
SUBDIRS = lang util

# testdirs must list all subdirectories
CHECKDIRS = awt lang math util io net security
# Don't recurse into subdirectories for testing since we do all 
# checking here in one command
TESTDIRS = 

RM = rm -f

default: runtests

all: check

check: runtests-this-start checkallspecs runtests-this-end

runtests-this: checkallspecs

# check all JML specs. - avoid the subdirectory java/tests
checkallspecs:
	if $(JML) $(JMLFLAGS) -R $(CHECKDIRS) > checkallspecs.out-ckd 2>&1 ; \
	then cat checkallspecs.out-ckd ; \
	else cat checkallspecs.out-ckd ; exit 1 ; \
	fi

# Clean up stuff
#
clean-this : cleanlocal

cleanlocal:
	$(RM) *.*-ckd core

clean-classes-this: remove-dot-class-files

include $(TOPDIR)/Make.ProjDefs
