# Configuration file for the Sphinx documentation builder.
#
# This file only contains a selection of the most common options. For a full
# list see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

import os

# To fix 'docs/contents.rst not found' errors we need this, see
# https://github.com/readthedocs/readthedocs.org/issues/2569

master_doc = 'index'
docs_root = 'https://docs.openmicroscopy.org'

# Variables used to define Github extlinks
if "SOURCE_BRANCH" in os.environ and len(os.environ.get('SOURCE_BRANCH')) > 0:
    branch = os.environ.get('SOURCE_BRANCH')
else:
    branch = 'develop'

if "SOURCE_USER" in os.environ and len(os.environ.get('SOURCE_USER')) > 0:
    user = os.environ.get('SOURCE_USER')
else:
    user = 'ome'

github_root = 'https://github.com/'
omero_github_root = github_root + user + '/openmicroscopy/'
docs_root = 'https://docs.openmicroscopy.org'
downloads_root = 'https://downloads.openmicroscopy.org'

# -- Project information -----------------------------------------------------

project = u'OMERO.insight'
copyright = u'2021, Open Microscopy Environment'
author = u'Open Microscopy Environment'

# The full version, including alpha/beta/rc tags
# The short X.Y version.
version = '5.6.1'
release = version



# -- General configuration ---------------------------------------------------

extlinks = {
    'source': (omero_github_root + 'blob/'+ branch + '/%s', ''),
    'devs_doc': (docs_root + '/contributing/%s', ''),
    'downloads': (downloads_root + '/%s', ''),
    'general_doc': (docs_root + '/omero/latest%s', ''),
    }

# Add any Sphinx extension module names here, as strings. They can be
# extensions coming with Sphinx (named 'sphinx.ext.*') or your custom
# ones.
extensions = [
    'sphinx.ext.autodoc',
    'sphinx.ext.extlinks',
]

# Add any paths that contain templates here, relative to this directory.
templates_path = ['_templates']

# List of patterns, relative to source directory, that match files and
# directories to ignore when looking for source files.
# This pattern also affects html_static_path and html_extra_path.
exclude_patterns = [u'_build', 'Thumbs.db', '.DS_Store']


# The name of the Pygments (syntax highlighting) style to use.
pygments_style = 'sphinx'

# -- Options for HTML output -------------------------------------------------

# The theme to use for HTML and HTML Help pages.  See the documentation for
# a list of builtin themes.
#
html_theme = 'default'

# Add any paths that contain custom static files (such as style sheets) here,
# relative to this directory. They are copied after the builtin static files,
# so a file named "default.css" will overwrite the builtin "default.css".
html_static_path = []

# Grouping the document tree into LaTeX files. List of tuples
# (source start file, target name, title,
#  author, documentclass [howto, manual, or own class]).
latex_documents = [
    (master_doc, 'OMEROInsight.tex', u'OMERO Insight Documentation',
     author, 'manual'),
]


# -- Options for manual page output ------------------------------------------

# One entry per manual page. List of tuples
# (source start file, name, description, authors, manual section).
man_pages = [
    (master_doc, 'omeroinsight', u'OMERO Insight Documentation',
     [author], 1)
]


# -- Options for Texinfo output ----------------------------------------------

# Grouping the document tree into Texinfo files. List of tuples
# (source start file, target name, title, author,
#  dir menu entry, description, category)
texinfo_documents = [
    (master_doc, 'OMEROInsight', u'OMERO Insight Documentation',
     author, 'OMEROInsight', 'One line description of project.',
     'Miscellaneous'),
]


# -- Options for Epub output -------------------------------------------------

# Bibliographic Dublin Core info.
epub_title = project

# The unique identifier of the text. This can be a ISBN number
# or the project homepage.
#
# epub_identifier = ''

# A unique identification for the text.
#
# epub_uid = ''

# A list of files that should not be packed into the epub file.
epub_exclude_files = ['search.html']