"""
Extract mappings from mondo-with-equivalents.json
Four ontologies: Orphanet, OMIM, DOID or EFO
"""

from owlready2 import *

onto = get_ontology('mondo-with-equivalents.owl').load()
MONDO_classes = [c for c in onto.classes() if c.name.startswith('MONDO_')]
print('MONDO classes %d' % len(MONDO_classes))

sources = ['Orphanet', 'OMIM', 'DOID', 'EFO']
mappings = dict()
for c in MONDO_classes:
    eqs = c.equivalent_to
    for i in range(len(sources)):
        for j in range(i + 1, len(sources)):
            ci, cj = None, None
            for e in eqs:
                if type(e) == ThingClass:
                    if sources[i] == e.name.split('_')[0]:
                        ci = e
                    if sources[j] == e.name.split('_')[0]:
                        cj = e
            if ci is not None and cj is not None:
                key = '%s-%s' % (sources[i], sources[j])
                mapping = '%s|%s' % (ci.iri, cj.iri)
                if key in mappings:
                    mappings[key].append(mapping)
                else:
                    mappings[key] = [mapping]

for key in mappings:
    print('%s: %d' % (key, len(mappings[key])))
    with open('%s.txt' % key, 'w') as f:
        for mapping in mappings[key]:
            f.write('%s\n' % mapping)
