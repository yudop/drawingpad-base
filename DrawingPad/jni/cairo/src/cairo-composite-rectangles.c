/* cairo - a vector graphics library with display and print output
 *
 * Copyright © 2009 Intel Corporation
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL") or, at your option, under the terms of the Mozilla
 * Public License Version 1.1 (the "MPL"). If you do not alter this
 * notice, a recipient may use your version of this file under either
 * the MPL or the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Suite 500, Boston, MA 02110-1335, USA
 * You should have received a copy of the MPL along with this library
 * in the file COPYING-MPL-1.1
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL or the MPL for
 * the specific language governing rights and limitations.
 *
 * The Original Code is the cairo graphics library.
 *
 * The Initial Developer of the Original Code is Red Hat, Inc.
 *
 * Contributor(s):
 *      Chris Wilson <chris@chris-wilson.co.uk>
 */

#include "cairoint.h"

#include "cairo-error-private.h"
#include "cairo-composite-rectangles-private.h"
#include "cairo-pattern-private.h"

/* A collection of routines to facilitate writing compositors. */

void _cairo_composite_rectangles_fini (cairo_composite_rectangles_t *extents)
{
    _cairo_clip_destroy (extents->clip);
}

static inline cairo_bool_t
_cairo_composite_rectangles_init (cairo_composite_rectangles_t *extents,
				  const cairo_rectangle_int_t *unbounded,
				  cairo_operator_t op,
				  const cairo_pattern_t *source,
				  const cairo_clip_t *clip)
{
    extents->clip = NULL;
    extents->destination = *unbounded;

    if (_cairo_clip_is_all_clipped (clip))
	return FALSE;

    extents->unbounded = extents->destination;
    if (clip != NULL) {
	if (! _cairo_rectangle_intersect (&extents->unbounded,
					  _cairo_clip_get_extents (clip)))
	    return FALSE;
    }

    extents->bounded = extents->unbounded;
    extents->is_bounded = _cairo_operator_bounded_by_either (op);

    _cairo_pattern_get_extents (source, &extents->source);
    if (extents->is_bounded & CAIRO_OPERATOR_BOUND_BY_SOURCE) {
	if (! _cairo_rectangle_intersect (&extents->bounded, &extents->source))
	    return FALSE;
    }

    return TRUE;
}

cairo_int_status_t
_cairo_composite_rectangles_init_for_paint (cairo_composite_rectangles_t *extents,
					    const cairo_rectangle_int_t *unbounded,
					    cairo_operator_t		 op,
					    const cairo_pattern_t	*source,
					    const cairo_clip_t		*clip)
{
    if (! _cairo_composite_rectangles_init (extents, unbounded,
					    op, source, clip))
    {
	return CAIRO_INT_STATUS_NOTHING_TO_DO;
    }

    extents->mask = *unbounded;

    extents->clip = _cairo_clip_reduce_for_composite (clip, extents);
    if (_cairo_clip_is_all_clipped (extents->clip))
	return CAIRO_INT_STATUS_NOTHING_TO_DO;

    return CAIRO_STATUS_SUCCESS;
}

static cairo_int_status_t
_cairo_composite_rectangles_intersect (cairo_composite_rectangles_t *extents,
				       const cairo_clip_t *clip)
{
    cairo_bool_t ret;

    ret = _cairo_rectangle_intersect (&extents->bounded, &extents->mask);
    if (! ret && extents->is_bounded & CAIRO_OPERATOR_BOUND_BY_MASK)
	return CAIRO_INT_STATUS_NOTHING_TO_DO;

    if (extents->is_bounded == (CAIRO_OPERATOR_BOUND_BY_MASK | CAIRO_OPERATOR_BOUND_BY_SOURCE))
	extents->unbounded = extents->bounded;

    extents->clip = _cairo_clip_reduce_for_composite (clip, extents);
    if (_cairo_clip_is_all_clipped (extents->clip))
	return CAIRO_INT_STATUS_NOTHING_TO_DO;

    return CAIRO_STATUS_SUCCESS;
}

cairo_int_status_t
_cairo_composite_rectangles_intersect_mask_extents (cairo_composite_rectangles_t *extents,
						    const cairo_box_t *box)
{
    cairo_rectangle_int_t mask;
    cairo_int_status_t status;
    cairo_clip_t *clip;

    _cairo_box_round_to_rectangle (box, &mask);
    if (mask.x == extents->mask.x &&
	mask.y == extents->mask.y &&
	mask.width  == extents->mask.width &&
	mask.height == extents->mask.height)
    {
	return CAIRO_INT_STATUS_SUCCESS;
    }

    _cairo_rectangle_intersect (&extents->mask, &mask);

    extents->mask = mask;
    clip = extents->clip;

    status = _cairo_composite_rectangles_intersect (extents, clip);

    if (clip != extents->clip)
	_cairo_clip_destroy (clip);

    return status;
}

cairo_int_status_t
_cairo_composite_rectangles_init_for_mask (cairo_composite_rectangles_t *extents,
					   const cairo_rectangle_int_t *unbounded,
					   cairo_operator_t		 op,
					   const cairo_pattern_t	*source,
					   const cairo_pattern_t	*mask,
					   const cairo_clip_t		*clip)
{
    if (! _cairo_composite_rectangles_init (extents, unbounded,
					    op, source, clip))
    {
	return CAIRO_INT_STATUS_NOTHING_TO_DO;
    }

    _cairo_pattern_get_extents (mask, &extents->mask);

    return _cairo_composite_rectangles_intersect (extents, clip);
}

cairo_int_status_t
_cairo_composite_rectangles_init_for_stroke (cairo_composite_rectangles_t *extents,
					     const cairo_rectangle_int_t *unbounded,
					     cairo_operator_t		 op,
					     const cairo_pattern_t	*source,
					     const cairo_path_fixed_t		*path,
					     const cairo_stroke_style_t	*style,
					     const cairo_matrix_t	*ctm,
					     const cairo_clip_t		*clip)
{
    if (! _cairo_composite_rectangles_init (extents, unbounded,
					    op, source, clip))
    {
	return CAIRO_INT_STATUS_NOTHING_TO_DO;
    }

    _cairo_path_fixed_approximate_stroke_extents (path, style, ctm, &extents->mask);

    return _cairo_composite_rectangles_intersect (extents, clip);
}

cairo_int_status_t
_cairo_composite_rectangles_init_for_fill (cairo_composite_rectangles_t *extents,
					   const cairo_rectangle_int_t *unbounded,
					   cairo_operator_t		 op,
					   const cairo_pattern_t	*source,
					   const cairo_path_fixed_t		*path,
					   const cairo_clip_t		*clip)
{
    if (! _cairo_composite_rectangles_init (extents, unbounded,
					    op, source, clip))
    {
	return CAIRO_INT_STATUS_NOTHING_TO_DO;
    }

    _cairo_path_fixed_approximate_fill_extents (path, &extents->mask);

    return _cairo_composite_rectangles_intersect (extents, clip);
}

cairo_int_status_t
_cairo_composite_rectangles_init_for_glyphs (cairo_composite_rectangles_t *extents,
					     const cairo_rectangle_int_t *unbounded,
					     cairo_operator_t		 op,
					     const cairo_pattern_t	*source,
					     cairo_scaled_font_t	*scaled_font,
					     cairo_glyph_t		*glyphs,
					     int			 num_glyphs,
					     const cairo_clip_t		*clip,
					     cairo_bool_t		*overlap)
{
    cairo_status_t status;

    if (! _cairo_composite_rectangles_init (extents, unbounded,
					    op, source, clip))
    {
	return CAIRO_INT_STATUS_NOTHING_TO_DO;
    }

    /* Computing the exact bbox and the overlap is expensive.
     * First perform a cheap test to see if the glyphs are all clipped out.
     */
    if (extents->is_bounded & CAIRO_OPERATOR_BOUND_BY_MASK) {
	_cairo_scaled_font_glyph_approximate_extents (scaled_font,
						      glyphs, num_glyphs,
						      &extents->mask);
	if (! _cairo_rectangle_intersect (&extents->bounded, &extents->mask))
	    return CAIRO_INT_STATUS_NOTHING_TO_DO;
    }

    status = _cairo_scaled_font_glyph_device_extents (scaled_font,
						      glyphs, num_glyphs,
						      &extents->mask,
						      overlap);
    if (unlikely (status))
	return status;

    return _cairo_composite_rectangles_intersect (extents, clip);
}

cairo_bool_t
_cairo_composite_rectangles_can_reduce_clip (cairo_composite_rectangles_t *composite,
					     cairo_clip_t *clip)
{
    cairo_rectangle_int_t extents;

    if (clip == NULL)
	return TRUE;

    /* XXX In the not a region case, we could still search through the boxes */
    if (! _cairo_clip_is_region (clip))
	return FALSE;

    extents = composite->destination;
    if (composite->is_bounded & CAIRO_OPERATOR_BOUND_BY_SOURCE)
	_cairo_rectangle_intersect (&extents, &composite->source);
    if (composite->is_bounded & CAIRO_OPERATOR_BOUND_BY_MASK)
	_cairo_rectangle_intersect (&extents, &composite->mask);

    return cairo_region_contains_rectangle (_cairo_clip_get_region (clip),
					    &extents) == CAIRO_REGION_OVERLAP_IN;
}