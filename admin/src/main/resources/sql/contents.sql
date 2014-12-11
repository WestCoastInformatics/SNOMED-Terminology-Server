--
-- This script is used to verify that removers properly clean
-- up the coresponding data they are intended to without orphans.
--
-- Written for MYSQL or Oracle
--
-- NOTE: "_aud" tables are not checked because some removers use
--      the framework.  The only way to completely get rid of audit
--      trail info is to delete the entire database and reload from scratch
--

-- terminology data
select 'concepts', count(*) from concepts;
select 'descriptions', count(*) from descriptions;
select 'relationships', count(*) from relationships;
select 'transitive_relationships', count(*) from relationships;

select 'association_referece_refset_members', count(*) from attribute_value_refset_members;
select 'attribute_value_refset_members', count(*) from attribute_value_refset_members;
select 'complex_map_refset_members', count(*) from complex_map_refset_members;
select 'language_refset_members', count(*) from language_refset_members;
select 'simple_map_refset_members', count(*) from simple_map_refset_members;
select 'simple_refset_members', count(*) from simple_refset_members;

select 'release_infos', count(*) from release_infos;

